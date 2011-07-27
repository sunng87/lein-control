(ns leiningen.control
  (:use [control.core :only (do-begin)]
           [leiningen.help :only (help-for)]
           [clojure.java.io :only [file]]))

(defn- get-config [project key]
  (get-in project [:control key]))

(defn- load-control-file [project]
  (try 
    (binding [*ns* (the-ns 'control.core)]
      (load-file
        (if-let [control-file-name (get-config project :control-file)]
          control-file-name "./control.clj")))
  (catch java.io.FileNotFoundException _)))

(defn- run-control [project args]
  (do
    (load-control-file project)
    (do-begin args)))

(defn init
  "Initialize clojure-control, create a sample control file in project home"
  [project & args]
  (let [control-file (file "." "control.clj")]
    (if-not (.exists control-file)
      (spit control-file 
        (str 
          "(defcluster :default-cluster\n"
          "  :clients [\n"
          "    {:host \"localhost\" :user \"root\"}\n"
          "  ])\n"
          "\n"
          "(deftask :date \"Get date\""
          "  []\n"
          "  (ssh \"date\"))\n")))))

(defn run
  "Run user-defined clojure-control tasks against certain cluster"
  [project & args]
  (run-control project args))

(defn control
  "Leiningen plugin for Clojure-Control"
  {:help-arglists '([subtask [cluster task [args...]]])
   :subtasks [#'init #'run]}
  ([project]
    (println (help-for "control")))
  ([project subtask & args]
    (case subtask
      "init" (apply init project args)
      "run" (apply run project args))))


