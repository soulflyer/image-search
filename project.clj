(defproject image-search "0.2.0"
  :description "Search for images containing specific metadata"
  :url "http://github.com/soulflyer/image-search"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.cli "1.0.214"]
                 [org.slf4j/slf4j-nop "2.0.6"]
                 [image-lib "0.2.5"]]

  :main image-search.command-line
  :bin {:name "image-search"
        :bin-path "~/bin"})
