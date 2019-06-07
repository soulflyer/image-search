(ns image-search.play
  (:refer-clojure :exclude [and or])
  (:require [image-lib.images      :refer [find-images find-all-images]]
            [image-lib.core        :refer [best-image]]
            [image-lib.keywords    :refer [find-sub-keywords]]
            [image-lib.helper      :refer [image-path image-paths]]
            [image-lib.preferences :refer [preference preference! preferences]]
            [image-lib.search      :refer [in eq lt le gt ge
                                           or and ]]
            [image-lib.file-helper :refer [write]]
            [image-search.core     :refer [all-images
                                           database
                                           db
                                           fullsize
                                           image-collection
                                           images
                                           keyword-collection
                                           large
                                           medium
                                           preferences-collection
                                           thumbnail
                                           open]]))


(comment
  ;; The main functions from image-lib are available here:
  (find-images db image-collection "ISO-Speed-Ratings" "640")
  (find-sub-keywords db keyword-collection "Iain Wood")

  ;; Also the preferences can be viewed and set.
  (preferences db preferences-collection)
  (preference db preferences-collection "external-viewer")
  (preference! db preferences-collection "external-viewer" "/usr/bin/open")

  ;; Usually we don't want all the details about the images we find
  ;; We can use map, and a metadata field name as a function to give us
  ;; just one piece of information:
  (map :Project (find-images db image-collection "ISO-Speed-Ratings" "640"))

  ;; In the case of :Project, it is probably more useful to turn the
  ;; sequence into a set:
  (set (map :Project (find-images db image-collection "ISO-Speed-Ratings" "640")))

  ;; We can also use the eq ge lt etc functions to filter a list of images
  (eq (find-images db image-collection "ISO-Speed-Ratings" "640") :Project "10-Road")

  ;; Rather than start with a find-images function and filter it, it may be
  ;; easier to start with the all-images function and just use a sucession
  ;; of filters to get the images we want:
  (count (eq
           (eq
             all-images
             :ISO-Speed-Ratings 640)
           :Exposure-Time 160))

  ;; The more complex the query, the harder it is to understand. Using the
  ;; -> syntax makes it very clear and easy to understand what is going on:
  (-> all-images
    (eq :ISO-Speed-Ratings 640)
    (eq :Exposure-Time 160)
    (open medium))

  ;; Note that if we use in to search a metadata field that contains a
  ;; string, we can use an incomplete case insensitive string
  (-> all-images
    (in :Model "phone")
    (eq :Year 2015)
    first)

  (class (-> all-images
           (in :Model "Nik")
           (eq :Year 2015)))

  ;; We can also use or
  (-> all-images
    (or
      (in :Model "phone")
      (in :Model "Nik"))
    (eq :ISO-Speed-Ratings 640)
    count)

  ;; and and. and is not necessary normally. Using -> with a series of filters is
  ;; effectiely doing an and. However it can be specified so that it can be used
  ;; inside an or.

  (-> all-images
    (or
      (in :Model "phone")
      (and (in :Model "Nik")
        (eq :Year 2016)))
    count)

  ;; images can be used instead of -> all-images
  (images
    (and (in :Model "Nik")
      (eq :ISO-Speed-Ratings 640))
    (open medium))

  ;; We can also output a list of pictures to a file. Note that the file is not
  ;; emptied first, up to you to rm it, if thats what you want. This example also uses
  ;; paths, which just outputs the path of each pic

  (images
    (in :Model "phone")
    (image-paths)
    (write "/tmp/phone-pics"))

  (images
    (in :Keywords "Ceratasoma trilobatum")
    (image-paths)
    (write "/tmp/ceratasoma.txt"))

  (images
    (in :Keywords "Unidentified nudibranch")
    (image-paths)
    (write "/tmp/unidentified-nudibranch.txt"))
  )
