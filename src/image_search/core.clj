(ns image-search.core
  (:require [image-lib.core     :refer [find-images
                                        find-all-images
                                        image-path
                                        image-paths
                                        best-image
                                        preference
                                        find-sub-keywords]]
            [monger                    [collection :as mc]
                                       [core :as mg]]
            [clojure.string     :refer [join replace]]
            [clojure.java.shell :refer [sh]]
            [clojure.set        :refer [union]])
  (:gen-class))

(def database "photos")
(def keyword-collection "keywords")
(def preferences-collection "preferences")
(def image-collection "images")
(def connection      (mg/connect))
(def db              (mg/get-db connection database))
(def all-images      (mc/find-maps db image-collection))
(def thumbnail       (preference db "preferences" "thumbnail-directory"))
(def medium          (preference db "preferences"    "medium-directory"))
(def large           (preference db "preferences"     "large-directory"))
(def fullsize        (preference db "preferences"  "fullsize-directory"))
(def external-viewer (preference db "preferences"     "external-viewer"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn clean-number-string
  "returns a number when given a string. Leading and trailing text and anything before a / character is removed"
  [x]
  (if (and x (not (= x "")))
    (replace
     (re-find #"[\d/]+" (str x))
     #"^.+/"
     "")
    ""))

(defmulti string-number-equals
  "a version of = that can compare numbers, strings or one of each"
  (fn [x y] (cond
             (or (nil? x) (nil? y)) :empty
             (and (instance? String x) (instance? String y)) :2strings
             :else :other)))
(defmethod string-number-equals :other [x y]
  (= (bigdec (clean-number-string x)) (bigdec (clean-number-string y))))
(defmethod string-number-equals :2strings [x y]
  (= x y))
(defmethod string-number-equals :empty [x y]
  (cond (and (nil? x) (nil? y)) true
        (or (= "" x) (= "" y)) true
        :else false))

(defn eq [image-seq meta-key meta-value]
  (filter #(string-number-equals (meta-key %) meta-value) image-seq))

(defmulti contains
  "returns true if haystack contains needle. This is case insensitive and matches substrings if haystack is a string"
  (fn [haystack needle]
    (class haystack)))
(defmethod contains java.lang.String
  [haystack needle]
  (if (re-find (re-pattern (str "(?i)" needle)) haystack)
    true
    false))
(defmethod contains clojure.lang.Sequential
  [haystack needle]
  (contains? (set haystack) needle))
(defmethod contains nil
  [haystack needle]
  false)

(defn in
  "returns a sequence containing all entries of image-seq where meta-key contains meta-value"
  [image-seq meta-key meta-value]
  (filter #(contains (meta-key %) meta-value)
          image-seq))

(defn gt [image-seq meta-key meta-value]
  (filter #(let [db-value (meta-key %)]
             (if db-value
               (>  (bigdec (clean-number-string (meta-key %)))
                   (bigdec (clean-number-string meta-value)))
               false))
          image-seq))

(defn lt [image-seq meta-key meta-value]
  (filter #(let [db-value (meta-key %)]
             (if db-value
               (<  (bigdec (clean-number-string db-value))
                   (bigdec (clean-number-string meta-value)))
               false))
          image-seq))

(defn ge [image-seq meta-key meta-value]
  (filter #(let [db-value (meta-key %)]
             (if db-value
               (>= (bigdec (clean-number-string db-value))
                   (bigdec (clean-number-string meta-value)))
               false))
          image-seq))

(defn le [image-seq meta-key meta-value]
  (filter #(if (meta-key %)
             (<= (bigdec (clean-number-string (meta-key %)))
                 (bigdec (clean-number-string meta-value)))
             false)
          image-seq))

(defn open [pics size]
  (sh "xargs" external-viewer
      :in (join " " (map #(str size "/" %)
                         (map image-path pics)))))

(defmacro or [coll form1 form2]
  `(union (-> ~coll ~form1)
          (-> ~coll ~form2)))

(defmacro and [coll form1 form2]
  `(-> ~coll ~form1 ~form2)
)
