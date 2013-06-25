(ns clojure-watch.example
  (:require [clojure-watch.core :refer [start-watch]]))

(start-watch [{:path "/home/derek/Desktop"
               :event-types [:create :modify :delete]
               :callback (fn [event filename] (println event filename))
               :options {:recursive true}}])
