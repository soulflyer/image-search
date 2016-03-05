(ns image-search.core
  (:require [image-lib.core :refer [find-images
                                    find-all-images
                                    image-path
                                    image-paths
                                    best-image
                                    preference]]
            [monger
             [collection :as mc]
             [core :as mg]]
            [clojure.string :refer [replace]])
  (:gen-class))

(def database "photos")
(def keyword-collection "keywords")
(def preferences-collection "preferences")
(def image-collection "images")
(def connection (mg/connect))
(def db (mg/get-db connection database))
(def all-images (mc/find-maps db image-collection))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn gt [meta-key meta-value image-seq]
  (filter #(< (bigdec (meta-key %)) (bigdec meta-value)) image-seq))

(defn lt [meta-key meta-value image-seq]
  (filter #(> (bigdec (meta-key %)) (bigdec meta-value)) image-seq))

(defn ge [meta-key meta-value image-seq]
  (filter #(<= (bigdec (meta-key %)) (bigdec meta-value)) image-seq))

(defn le [meta-key meta-value image-seq]
  (filter #(>= (bigdec (meta-key %)) (bigdec meta-value)) image-seq))

(defmulti string-number-equals
  "a version of = that can compare numbers, strings or one of each"
  (fn [x y] (cond
             (or (nil? x) (nil? y)) :empty
             (and (instance? String x) (instance? String y)) :2strings
             :else :other)))
(defmethod string-number-equals :other [x y]
  (= (bigdec x) (bigdec y)))
(defmethod string-number-equals :2strings [x y]
  (= x y))
(defmethod string-number-equals :empty [x y]
  (cond (and (nil? x) (nil? y)) true
        (or (= "" x) (= "" y)) true
        :else false))

(defn eq [meta-key meta-value image-seq]
  (filter #(string-number-equals (meta-key %) meta-value) image-seq))

(find-images db image-collection "ISO-Speed-Ratings" "640")
(map :Project (find-images db image-collection "ISO-Speed-Ratings" "640"))
(set (map :Project (find-images db image-collection "ISO-Speed-Ratings" "640")))
(filter #(string-number-equals (:Project %) "10-Road") (find-images db image-collection "ISO-Speed-Ratings" "640"))
(eq :Project "10-Road" (find-images db image-collection "ISO-Speed-Ratings" "640"))
