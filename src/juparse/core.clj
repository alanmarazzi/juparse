(ns juparse.core
  (:require
   [cheshire.core :as j]
   [clojure.java.io :as io]
   [clojure.string :as s]
   [clojure.set :as st]
   [clojure.edn :as edn]
   [me.raynes.fs :as fs]
   [clojure.tools.cli :refer [cli]])
  (:gen-class))

(def CONFIG
  [["-i" "--input" :default "." :flag false]
   ["-o" "--output" :default "requirements.txt" :flag false]])

(defn read-nb
  [path]
  (with-open [nb (io/reader path)]
    (j/parse-stream nb true)))

(defn read-standard
  []
  (set
   (s/split-lines
    (slurp
     (io/resource "standard.txt")))))

(defn read-package-map
  []
  (edn/read-string
   (slurp
    (io/resource "data.edn"))))

(defn find-nbs
  [path]
  (fs/find-files path #".+\.ipynb"))

(defn write-packages
  [path packages]
  (with-open [w (io/writer path)]
    (doseq [l packages]
      (.write w (str l "\n")))))

(def standard (read-standard))

(def package-map
  (read-package-map))

(def get-code
  (filter #(= "code" (:cell_type %))))

(def get-source
  (mapcat :source))

(def get-imports
  (filter #(s/includes? % "import")))

(def clean-imports
  (comp
   (map s/trim-newline)
   (map #(s/split % #" "))))

(def package-name
  (map second))

(def clean-package-name
  (comp
   (map #(s/split % #"\."))
   (map first)))

(def clean-standard
  (filter (complement standard)))

(defn map-package-name
  [package-name]
  (get package-map package-name package-name))

(defn parse-nb
  [path]
  (let [nb (:cells (read-nb path))]
    (set
     (into [] (comp
               get-code
               get-source
               get-imports
               clean-imports
               package-name
               clean-package-name
               clean-standard
               (map map-package-name)) nb))))

(defn parse-all
  [path]
  (let [nbs (find-nbs path)]
    (apply st/union (map parse-nb nbs))))

(defn parse-and-write
  [in out]
  (write-packages out (parse-all in)))

(defn -main
  [& args]
  (let [[opts args banner] (apply cli args CONFIG)]
    (parse-and-write (:input opts) (:output opts))))
