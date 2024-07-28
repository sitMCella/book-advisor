(defproject bookadvisorbackend "0.1.0-SNAPSHOT"
  :description "Book Advisor backend project"
  :url "https://github.com/sitMCella/book-advisor"
  :license {:name "MIT License"
            :url "https://github.com/sitMCella/book-advisor/blob/main/LICENSE"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.12.2"]
                 [ring/ring-defaults "0.5.0"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.7.1"]
                 [cheshire "5.13.0"]
                 [com.stuartsierra/component "1.1.0"]
                 [com.github.seancorfield/next.jdbc "1.3.939"] 
                 [org.postgresql/postgresql "42.6.0"]
                 [log4j "1.2.15" :exclusions [[javax.mail/mail :extension "jar"]
                                              [javax.jms/jms :classifier "*"]
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]
                 ]
  :main ^:skip-aot bookadvisorbackend.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
