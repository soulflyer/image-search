(ns image-search.command-line
  (:refer-clojure :exclude [or and])
  (:require [image-search.core :refer [open all-images ifeq ifin ]]
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

(defn print-count [pics]
  (println (count pics)))

(defn -main
  "Searches for image details from a mongo database"
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        output-function (if (:count options) print-count open)]

    (if (:help options)
      (println (str "Usage:\nfind-images [options] keyword\n\nvoptions:\n" summary))
      (-> all-images
          (ifeq :ISO-Speed-Ratings   (:iso options))
          (ifeq :Year               (:year options))
          (ifeq :Month             (:month options))
          (ifin :Project         (:project options))
          (ifeq :F-Number       (:aperture options))
          (ifin :Keywords        (:keyword options))
          (ifin :Model             (:model options))
          (output-function)))))
