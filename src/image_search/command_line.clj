(ns image-search.command-line
  (:refer-clojure :exclude [or and])
  (:require [image-search.core :refer [open all-images]]
            [image-lib.search  :refer [eq in]]
            [image-lib.helper  :refer [image-paths]]
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
   ["-k" "--keyword KEYWORD" "search for KEYWORD"]
   ["-o" "--open"]])

(defn print-count [pics]
  (println (count pics)))

(defn print-paths
  "doc-string"
  [images]
  (doall (map println (image-paths images))))

(defn open-and-exit [images]
  (doall
    (open images)
    (shutdown-agents)))

(defn -main
  "Searches for image details from a mongo database"
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        output-function (cond
                          (:count options)
                          print-count
                          (:open options)
                          open-and-exit
                          :else
                          print-paths)]

    (if (:help options)
      (println (str
                "Does simple searches for images using the most popular exif/iptc data\n"
                "and Year, Month. Project values.\n"
                "For more complex searches involving nested AND and OR or more esoteric\n"
                "exif/iptc data, start the repl and use the image-search.core library.\n\n"
                "Usage:\nimage-search [options] keyword\n\nvoptions:\n"
                summary))
      (-> all-images
          (eq :ISO-Speed-Ratings   (:iso options))
          (eq :Year               (:year options))
          (eq :Month             (:month options))
          (in :Project         (:project options))
          (eq :F-Number       (:aperture options))
          (in :Keywords        (:keyword options))
          (in :Model             (:model options))
          (output-function)))))
