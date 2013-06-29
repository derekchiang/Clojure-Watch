(ns clojure-watch.core
  (:require [clojure.contrib.import-static :as import-static])
  (:import (java.nio.file WatchService Paths FileSystems)))

(import-static/import-static java.nio.file.StandardWatchEventKinds
                             ENTRY_CREATE
                             ENTRY_DELETE
                             ENTRY_MODIFY)

(defn register [{:keys [path event-types callback options] :as spec}
                watcher keys]
  (letfn [(register-helper
           [{:keys [path event-types callback options]} watcher keys]
           (let [; make-array is needed because Paths/get is a variadic method
                 ; Java compiler handles variadic method automatically, but when
                 ; using Clojure it's necessary to manually supply an array at
                 ; the end.
                 dir (Paths/get path (make-array String 0))
                 types (reduce (fn [acc type]
                                 (case type
                                   :create (conj acc ENTRY_CREATE)
                                   :delete (conj acc ENTRY_DELETE)
                                   :modify (conj acc ENTRY_MODIFY)))
                               []
                               event-types)
                 key (.register dir watcher (into-array types))]
             (assoc keys key [dir callback])))]
    (register-helper spec watcher keys)))

(defn start-watch [specs]
  (letfn [(handle-recursive
           [specs]
           (reduce
            (fn [acc
                 {:keys [path event-types callback options bootstrap] :as spec}]
              (if bootstrap
                (bootstrap path))
              (if (:recursive options)
                (let [f (clojure.java.io/file path)
                      fs (file-seq f)
                      acc (ref acc)]
                  (do
                    (doall
                     (pmap
                      (fn [file]
                        (if (.isDirectory file)
                          (dosync
                           (commute
                            acc
                            #(conj % (assoc spec :path (str file))))))) fs))
                    (deref acc)))
                (conj acc spec)))
            []
            specs))]
    (let [specs (handle-recursive specs)
          watcher (.. FileSystems getDefault newWatchService)
          keys (reduce (fn [keys spec]
                         (register spec watcher keys)) {} specs)]
      (letfn [(kind-to-key [kind]
                           (case kind
                             "ENTRY_CREATE" :create
                             "ENTRY_MODIFY" :modify
                             "ENTRY_DELETE" :delete))
              (watch [watcher keys]
                     (let [key (.take watcher)
                           [dir callback] (keys key)]
                       (do
                         (doseq [event (.pollEvents key)]
                           (let [kind (kind-to-key (.. event kind name))
                                 name (->> event
                                           .context
                                           (.resolve dir)
                                           str)]
                             ; Run callback in another thread
                             @(future (callback kind name))))
                         (.reset key)
                         (recur watcher keys))))]
        (watch watcher keys)))))