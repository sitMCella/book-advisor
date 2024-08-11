(ns bookadvisorbackend.core
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [GET POST PUT DELETE let-routes]]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :as ring-defaults]
            [bookadvisorbackend.book-ctl :as book-ctl]
            [bookadvisorbackend.database-manager :as database-manager])
  (:gen-class))

(defrecord Application [config
                        database
                        state]
  component/Lifecycle
  (start [this]
    (assoc this :state "Running"))
  (stop  [this]
    (assoc this :state "Stopped")))

(defn application-component
  "Return the application component.
  The application depends on the database connection."
  [config]
  (component/using (map->Application {:config config})
                   [:database]))

(defn middleware
  "This middleware runs for every request and can execute before/after logic."
  [handler]
  (fn [req]
    (let [resp (handler req)]
      resp)))

(defn- add-app-component
  "Middleware to add your application component into the request. Use
  the same qualified keyword in your controller to retrieve it."
  [handler application]
  (fn [req]
    (handler (assoc req :application/component application))))

(defn middleware-stack
  "Given the application component and middleware, return a standard stack of
  Ring middleware for a web application."
  [app-component app-middleware]
  (fn [handler]
    (-> handler
        (app-middleware)
        (add-app-component app-component)
        (ring-defaults/wrap-defaults (-> ring-defaults/site-defaults
                                         (assoc-in [:security :anti-forgery] false)
                                         (assoc-in [:proxy] true))))))

(defn router-handler
  "Given the application component, return the middleware for routing."
  [application]
  (let-routes [wrap (middleware-stack application #'middleware)]
    (GET    "/api/projects"                   []   (wrap #'book-ctl/get-projects))
    (POST   "/api/projects"                   []   (wrap #'book-ctl/create-project))
    (PUT    "/api/projects"                   []   (wrap #'book-ctl/update-project))
    (GET    "/api/projects/:id"               []   (wrap #'book-ctl/get-project))
    (DELETE "/api/projects/:id"               []   (wrap #'book-ctl/delete-project))
    (GET    "/api/projects/:id/chapters"      []   (wrap #'book-ctl/get-chapters))
    (POST   "/api/projects/:id/chapters"      []   (wrap #'book-ctl/create-chapter))
    (PUT    "/api/projects/:id/chapters"      []   (wrap #'book-ctl/update-chapter))
    (DELETE "/api/projects/:id/chapters/:id"  []   (wrap #'book-ctl/delete-chapter))
    (GET    "/api/projects/:id/plots"         []   (wrap #'book-ctl/get-plots))
    (POST   "/api/projects/:id/plots"         []   (wrap #'book-ctl/create-plot))
    (PUT    "/api/projects/:id/plots"         []   (wrap #'book-ctl/update-plot))
    (DELETE "/api/projects/:id/plots/:id"     []   (wrap #'book-ctl/delete-plot))
    (GET    "/api/projects/:id/scenes"        []   (wrap #'book-ctl/get-scenes))
    (POST   "/api/projects/:id/scenes"        []   (wrap #'book-ctl/create-scene))
    (PUT    "/api/projects/:id/scenes"        []   (wrap #'book-ctl/update-scene))
    (GET    "/api/projects/:id/scenes/:id"    []   (wrap #'book-ctl/get-scene))
    (PUT    "/api/projects/:id/scenes/:id"    []   (wrap #'book-ctl/update-scene-value))
    (DELETE "/api/projects/:id/scenes/:id"    []   (wrap #'book-ctl/delete-scene))
    (GET    "/reset"                          []   (wrap #'book-ctl/reset-changes))
    (route/resources "/")
    (route/not-found "Not Found")))

(defrecord WebServer [handler-fn port
                      application
                      http-server shutdown]
  component/Lifecycle
  (start [this]
    (if http-server
      this
      (assoc this
             :http-server (run-jetty (handler-fn application)
                                     {:port port :join? false})
             :shutdown (promise))))
  (stop  [this]
    (if http-server
      (do
        (.stop http-server)
        (deliver shutdown true)
        (assoc this :http-server nil))
      this)))

(defn web-server
  "Return a WebServer component that depends on the application.
  The handler-fn is a function that accepts the application (Component) and
  returns a fully configured Ring handler (with middeware)."
  [handler-fn port]
  (component/using (map->WebServer {:handler-fn handler-fn
                                    :port port})
                   [:application]))

(defn new-system
  "Build a default system to run. In the REPL:
  (def system (new-system 8888))
  (alter-var-root #'system component/start)
  (alter-var-root #'system component/stop)
  See the Rich Comment Form below."
  ([port] (new-system port true))
  ([port repl]
   (component/system-map :application (application-component {:repl repl})
                         :database    (database-manager/setup-database)
                         :web-server  (web-server #'router-handler port))))

(comment
  (def system (new-system 8888))
  (alter-var-root #'system component/start)
  (alter-var-root #'system component/stop))

(defonce ^:private
  ^{:doc "This exists so that if you run a socket REPL when
  you start the application, you can get at the running
  system easily."}
  repl-system
  (atom nil))

(defn -main
  [& [port]]
  (let [port (or port (get (System/getenv) "PORT" 8080))
        port (cond-> port (string? port) Integer/parseInt)]
    (println "Starting up on port" port)
    (-> (component/start (new-system port false))
        (->> (reset! repl-system))
        :web-server :shutdown deref)))