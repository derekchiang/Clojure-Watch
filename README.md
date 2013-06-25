# clojure-watch

The most user-friendly file-system watch library written in Clojure, built on top of the [Java 7 WatchEvent API](http://docs.oracle.com/javase/tutorial/essential/io/notification.html).

## Usage

Firstly, add this to your dependencies:

```clojure
[clojure-watch "0.1.0"]
```

Example:

```clojure
(ns clojure-watch.example
  (:require [clojure-watch.core :refer [start-watch]]))

(start-watch [{:path "/path/to/a/directory"
               :event-types [:create :modify :delete]
               :callback (fn [event filename] (println event filename))
               :options {:recursive true}}])
```

You call `start-watch` with a collection of specifications.  A specification is a map with four entries:

1. `:path`.  The path to a directory that you want to watch.
2. `:event-types`.  A collection of the types of events that you want to watch.  Possible values include `:create`, `modify`, and `create`.
3. `:callback`.  A callback function to be invoked when an event occurs.  The function should accept two arguments, the first one being the type of the event that happened (`:create`, `:modify`, or `:delete`), and the second one being the full path to the file to which the event happened.
4. `:options`.  Currently the only available option is `:recursive`.  When it's set to true, all sub-directories will be watched.

## License

[WTFPL](http://www.wtfpl.net/).
