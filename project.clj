(defproject image-search "0.1.0"
  :description "Search for images containing specific metadata"
  :url "http://github.com/soulflyer/image-search"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.slf4j/slf4j-nop "1.7.26"]
                 [image-lib "0.2.5-SNAPSHOT"]]

  :main image-search.command-line
  :bin {:name "image-search"
        :bin-path "~/bin"})
