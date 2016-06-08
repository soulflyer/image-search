(ns image-search.core
  (:require [image-lib.core     :refer [find-images
                                        find-all-images
                                        image-path
                                        image-paths
                                        best-image
                                        preference
                                        preferences
                                        preference!
                                        find-sub-keywords]]
            [monger                    [collection :as mc]
                                       [core :as mg]]
            ;;[clojure.string     :refer [join replace]]
            [clojure.string     :as str]
            [clojure.java.shell :refer [sh]]
            [clojure.set        :refer [union]]
            [clojure.tools.cli  :refer :all])
  (:gen-class))

(def cli-options
  [["-c" "--count" "Counts the results"]
   ["-D" "--database DATABASE" "specifies database to use"
    :default "photos"]
   ["-I" "--image-collection IMAGE-COLLECTION" "specifies the image collection"
    :default "images"]
   ["-K" "--keyword-collection KEYWORD-COLLECTION" "specifies the keyword collection"
    :default "keywords"]
   ["-h" "--help"]
   ["-i" "--iso ISO" "Search on ISO value"]
   ["-s" "--shutter SHUTTER-SPEED" "search on SHUTTER-SPEED"]
   ["-f" "--aperture APERTURE" "search on APERTURE"]
   ["-y" "--year YEAR" "search on YEAR"]
   ["-m" "--month MONTH" "search on MONTH"]
   ["-M" "--model MODEL" "search by camera model"]
   ["-p" "--project PROJECT" "search photos in PROJECT"]
   ["-k" "--keyword KEYWORD" "search for KEYWORD"]])


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



(defn clean-number-string
  "returns a number when given a string. Leading and trailing text and anything before a / character is removed"
  [x]
  (if (and x (not (= x "")))
    ;; This regex pulls out a substing containing only digits and dots.
    (re-find #"[\d\.]+"
             (str/replace (str x)
                      ;;This regex removes a leading 1/ or f/
                      #"^[fF1]/"
                      ""))
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

(defn open
  ([pics]
   (open pics medium))
  ([pics size]
   (sh "xargs" external-viewer
       :in (str/join " " (map #(str size "/" %)
                          (map image-path pics))))))

(defn paths
  "given a collection of pics, return just the paths"
  [pics]
  (map image-path pics))

(defn write
  "Append the collection 'things' to file 'file-name' one per line"
  [things file-name]
  (map #(spit file-name (str % "\n") :append true) things))


(defmacro or [coll & forms]
  (if (seq forms)
    `(union (-> ~coll ~(first forms)) (or ~coll ~@(rest forms)))
    #{}))

(defmacro and [& forms]
  `(-> ~@forms)
)

(defmacro find [& forms]
  `(-> all-images
       ~@forms))

(defn ifeq
  [a b & c]
  (if (nil? (first c))
    a
    (eq a b c)))

(defn ifin
  [a b & c]
  (if (nil? (first c))
    a
    (in a b (first c))))

(defn print-count [pics]
  (println (count pics)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        output-function (if (:count options) print-count open)]
    ;; (println (str "output-function " output-function))
    (if (:help options)
      (println (str "Usage:\nfind-images [options] keyword\n\nvoptions:\n" summary)))
    (if (:iso options)
      (println (str "ISO " (:iso options))))
    (if (:shutter options)
      (println (str "Shutter speed " (:shutter options))))
    (if (:aperture options)
      (println (str "Aperture " (:aperture options))))
    (if (:year options)
      (println (str "Year " (:year options))))
    (if (:month options)
      (println (str "Month " (:month options))))
    (if (:project options)
      (println (str "Project " (:project options))))
    (if (:keyword  options)
      (println (str "Keyword " (:keyword options))))

    (if (:count options)
      (println "Count selected"))
    (-> all-images
        (ifeq :ISO-Speed-Ratings   (:iso options))
        (ifeq :Year               (:year options))
        (ifeq :Month             (:month options))
        (ifin :Project         (:project options))
        (ifeq :F-Number       (:aperture options))
        (ifin :Keywords        (:keyword options))
        (ifin :Model             (:model options))
        (output-function))))
