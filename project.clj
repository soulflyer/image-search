(defproject image-search "0.1.0-SNAPSHOT"
  :description "Search for images containing specific metadata"
  :url "http://github.com/soulflyer/image-search"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [image-lib "0.1.1-SNAPSHOT"]]
  :main ^:skip-aot image-search.core
  :profiles {:uberjar {:aot :all}}
  :bin {:name "image-search"
        :bin-path "~/bin"}
  :jvm-opts ["-Xdock:name=Image-Search"])
