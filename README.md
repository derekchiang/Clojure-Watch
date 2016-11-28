# clojure-watch

The most user-friendly file-system watch library written in Clojure, built on top of the [Java 7 WatchEvent API](http://docs.oracle.com/javase/tutorial/essential/io/notification.html).

## Installation

Add this to your dependencies:

```clojure
[clojure-watch "LATEST"]
```

Check out [Clojars](https://clojars.org/clojure-watch) for more specific installation instructions. 

## Usage

An example:

```clojure
(ns clojure-watch.example
  (:require [clojure-watch.core :refer [start-watch]]))

(start-watch [{:path "/some/valid/path"
               :event-types [:create :modify :delete]
               :bootstrap (fn [path] (println "Starting to watch " path))
               :callback (fn [event filename] (println event filename))
               :options {:recursive true}}])
```

You call `start-watch` with a collection of specifications.  A specification is a map with five entries:

1. `:path`.  The path to a directory that you want to watch.
2. `:event-types`.  A collection of the types of events that you want to watch.  Possible values include `:create`, `:modify`, and `:delete`.
3. `:bootstrap` (optional).  A function to be run only once when `start-watch` is invoked.  The function should accept one argument: the path given in the spec.
4. `:callback`.  A callback function to be invoked when an event occurs.  The function should accept two arguments, the first one being the type of the event that happened (`:create`, `:modify`, or `:delete`), and the second one being the full path to the file to which the event happened.
5. `:options`.  A map of options.  Currently the only available option is `:recursive`.  If it's set to true, all sub-directories will be watched.

### Stop the watch

`start-watch` returns a function that can be called to stop the watch.  For example:

(let [stop-watch (start-watch [{
    :path "/some/valid/path"
    :event-types [:create :modify :delete]
    :callback (fn [event filename] (println event filename))}])] ; start the watch
  (Thread/sleep 20000) ; manipulate files on the path
  (stop-watch)) ; stop the watch

## License

[WTFPL](http://www.wtfpl.net/).
