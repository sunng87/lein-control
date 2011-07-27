(ns leiningen.control
  (:use [leiningen.help :only (help-for)])
  (:use [leiningen.compile :only (eval-in-project)])
  (:use [control.core :only (do-begin)]))

(defn- get-config [project key]
  (get-in project [:control key]))

(defn- load-control-file [project]
  (try (binding [*ns* (the-ns 'control.core)]
            (load-file
              (if-let [control-file-name (get-config project :control-file)]
                control-file-name "./control.clj")))
  (catch java.io.FileNotFoundException _)))

(defn control
  "run user-defined clojure-control tasks"
  {}
  ([project]
    (println (help-for "control")))
  ([project & args]
    (load-control-file project)
    (do-begin args)))

