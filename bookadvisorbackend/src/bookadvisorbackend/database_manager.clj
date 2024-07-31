(ns bookadvisorbackend.database-manager
  (:require [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(def ^:private database-connection
  "Database connection specification."
  {:dbtype "postgresql"
   :dbname (get (System/getenv) "POSTGRES_DB" "postgres")
   :user (get (System/getenv) "POSTGRES_USER" "postgres")
   :password (get (System/getenv) "POSTGRES_PASSWORD" "postgres")
   :host "db"
   :port "5432"})

(def ^:private chapters
  "List of chapters"
  ["Chapter 1" "Chapter 2" "Chapter 3" "Chapter 4"])

(def ^:private plots
  "List of plots"
  ["Plot 1" "Subplot"])

(def ^:private initial-scenes
  "Seed the database with initial scenes."
  [{:title "Scene 1"
    :extract "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :value "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :chapter_id 1
    :plot_id 1},
   {:title "Scene 2"
    :extract "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :value "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :chapter_id 2
    :plot_id 1},
   {:title "Scene 3"
    :extract "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :value "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :chapter_id 2
    :plot_id 2},
   {:title "Scene 4"
    :extract "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :value "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :chapter_id 3
    :plot_id 2},
   {:title "Scene 5"
    :extract "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :value "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :chapter_id 4
    :plot_id 1}])

(defn- populate
  "Create the database tables and populate it. No action if the
  database tables already exist."
  [db db-type]
  (try
    (jdbc/execute-one! (db)
                       [(str "
create table chapters (
  id          SERIAL PRIMARY KEY,
  name        varchar(32)
)")])
    (jdbc/execute-one! (db)
                       [(str "
create table plots (
  id          SERIAL PRIMARY KEY,
  name        varchar(32)
)")])
    (jdbc/execute-one! (db)
                       [(str "
create table scenes (
  id           SERIAL PRIMARY KEY,
  title        varchar(32),
  extract      text,
  value        text,
  chapter_id   integer REFERENCES chapters ON DELETE CASCADE,
  plot_id      integer REFERENCES plots ON DELETE CASCADE
)")])
    (println "Created tables.")
    (try
      (println "Trying to populate tables...")
      (doseq [chapter chapters]
        (sql/insert! (db) :chapters {:name chapter}))
      (doseq [plot plots]
        (sql/insert! (db) :plots {:name plot}))
      (doseq [scene initial-scenes]
        (sql/insert! (db) :scenes scene))
      (println "Populated database with initial data.")
      (catch Exception e
        (println "Exception:" (ex-message e))
        (println "Unable to populate the initial data.")))
    (catch Exception e
      (println "The database is already setup.")
      (println "Exception:" (ex-message e)))))

(defrecord Database [db-spec
                     datasource]

  component/Lifecycle
  (start [this]
    (if datasource
      this
      (let [this+ds (assoc this :datasource (jdbc/get-datasource db-spec))]
        (populate this+ds (:dbtype db-spec))
        this+ds)))
  (stop [this]
    (assoc this :datasource nil))
  clojure.lang.IFn
  (invoke [_] datasource))

(defn setup-database [] (map->Database {:db-spec database-connection}))
