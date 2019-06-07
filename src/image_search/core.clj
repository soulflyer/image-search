(ns image-search.core
  (:refer-clojure :exclude [or and])
  (:require [monger                [collection :as mc] [core :as mg]]
            [clojure.string        :as str]
            [clojure.java.shell    :refer [sh]]
            [image-lib.preferences :refer [preference]]
            [image-lib.helper      :refer [image-path]]
            [image-lib.images      :refer [open-images]])
  (:gen-class))


(def database               "photos")
(def keyword-collection     "keywords")
(def preferences-collection "preferences")
(def image-collection       "images")
(def connection      (mg/connect))
(def db              (mg/get-db connection database))
(def all-images      (mc/find-maps db image-collection))
(def thumbnail       (preference db "preferences" "thumbnail-directory"))
(def medium          (preference db "preferences"    "medium-directory"))
(def large           (preference db "preferences"     "large-directory"))
(def fullsize        (preference db "preferences"  "fullsize-directory"))
(def external-viewer (preference db "preferences"     "external-viewer"))


(defn open
  ([pics]
   (open pics medium))
  ([pics size]
   (open-images pics size external-viewer)))


(defmacro images-> [& forms]
  `(-> all-images
     ~@forms))


(defmacro images [& forms]
  `(-> all-images
     ~@forms))
