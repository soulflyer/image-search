(ns image-search.core
  (:require [image-lib.core :refer [find-images
                                    find-all-images
                                    image-path
                                    image-paths
                                    best-image
                                    preference
                                    find-sub-keywords]]
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
  "filter passes any entry that contains the given string"
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


(find-images db image-collection "ISO-Speed-Ratings" "640")
(map :Project (find-images db image-collection "ISO-Speed-Ratings" "640"))
(set (map :Project (find-images db image-collection "ISO-Speed-Ratings" "640")))
(filter #(string-number-equals (:Project %) "10-Road") (find-images db image-collection "ISO-Speed-Ratings" "640"))
(eq (find-images db image-collection "ISO-Speed-Ratings" "640") :Project "10-Road")
(replace (re-find #"[\d/]+" "100px") #"^.+/" "")

(count (eq
        (eq
         all-images
         :ISO-Speed-Ratings 640)
        :Exposure-Time 160))

(-> all-images
    (eq :ISO-Speed-Ratings 640)
    (eq :Exposure-Time 160))

(-> all-images
    (in :Model "NIK")
    (eq :Year 2015)
    count)
(-> all-images
    (in :Keywords "Me")
    count)

(map image-path (-> all-images (lt :ISO-Speed-Ratings 64)))
(map image-path (-> all-images
                    (lt :ISO-Speed-Ratings 64)
                    (ge :Exposure-Time 4000)))

(find-sub-keywords db keyword-collection "Iain Wood")
(if (re-find #"the" "hello hello") true false)
