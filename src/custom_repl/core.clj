(ns custom-repl.core
  (:require [clojure.main :as main]))

(defn init-function []
  (println "Type an expression to evaluate it. Type 'exit' to quit.\n"))

(defn need-prompt-function []
  (if (instance? clojure.lang.LineNumberingPushbackReader *in*)
    #(.atLineStart *in*)
    #(identity true)))

(defn prompt-function []
  (printf "λ> "))

(defn read-function [request-prompt request-exit]
  (or ({:line-start request-prompt :stream-end request-exit}
       (main/skip-whitespace *in*))
      (let [line (read-line)] 
        (cond 
          (#{"exit" "q" ":q" "quit" "close"} line) 
          (do (println "Bye!") request-exit)
          (#{"help" "h"} line) 
          (do (println "Enter a simple math expression.") request-prompt)
          :else 
          (re-find #"^(\d+)\s*([\+\-\*\/])\s*(\d+)$" line)))))

(defn eval-function [[_ x op y]]
  (if (#{"+" "-" "*" "/"} op)
    (({"+" + "-" - "*" * "/" /} op)
     (Integer. x)
     (Integer. y))
    "Unsupported operation."))

(defn caught-function [e]
  (println "=> Sorry, that didn't work.")
  (println "=> Cause:" (ex-message e)))

(def repl-options
  [:init init-function
   :need-prompt need-prompt-function
   :prompt prompt-function
   :read read-function
   :eval eval-function
   :print #(println "=>" %)
   :caught caught-function])

(defn -main [& args]
  (apply main/repl repl-options))
