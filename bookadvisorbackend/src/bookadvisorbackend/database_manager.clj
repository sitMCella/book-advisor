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

(def ^:private projects
  "List of projects"
  [{:name "Project 1" :tags ["tag1_1", "tag1_2", "tag1_3"]} {:name "Project 2" :tags ["tag2_1"]} {:name "Project 3" :tags ["tag3_1", "tag3_2"]}])

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
    :plot_id 1
    :project_id 1},
   {:title "Scene 2"
    :extract "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :value "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :chapter_id 2
    :plot_id 1
    :project_id 1},
   {:title "Scene 3"
    :extract "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :value "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :chapter_id 2
    :plot_id 2
    :project_id 1},
   {:title "Scene 4"
    :extract "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :value "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :chapter_id 3
    :plot_id 2
    :project_id 1},
   {:title "Scene 5"
    :extract "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :value "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    :chapter_id 4
    :plot_id 1
    :project_id 1}])

(defn- populate
  "Create the database tables and populate it. No action if the
  database tables already exist."
  [db]
  (try
    (jdbc/execute-one! (db)
                       [(str "
    create table projects (
      id          SERIAL PRIMARY KEY,
      name        varchar(32),
      description text,
      tags        varchar[]
    )")])
    (jdbc/execute-one! (db)
                       [(str "
create table chapters (
  id          SERIAL PRIMARY KEY,
  name        varchar(32),
  project_id  integer REFERENCES projects ON DELETE CASCADE
)")])
    (jdbc/execute-one! (db)
                       [(str "
create table plots (
  id          SERIAL PRIMARY KEY,
  name        varchar(32),
  project_id  integer REFERENCES projects ON DELETE CASCADE
)")])
    (jdbc/execute-one! (db)
                       [(str "
create table scenes (
  id           SERIAL PRIMARY KEY,
  title        varchar(32),
  extract      text,
  value        text,
  chapter_id   integer REFERENCES chapters ON DELETE CASCADE,
  plot_id      integer REFERENCES plots ON DELETE CASCADE,
  project_id   integer REFERENCES projects ON DELETE CASCADE
)")])
    (println "Created tables.")
    (try
      (println "Trying to populate tables...")
      (doseq [project projects]
        (sql/insert! (db) :projects {:name (:name project) :description "this is a book." :tags (into-array String (:tags project))}))
      (doseq [chapter chapters]
        (sql/insert! (db) :chapters {:name chapter :project_id 1}))
      (doseq [plot plots]
        (sql/insert! (db) :plots {:name plot :project_id 1}))
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
        (populate this+ds)
        this+ds)))
  (stop [this]
    (assoc this :datasource nil))
  clojure.lang.IFn
  (invoke [_] datasource))

(defn setup-database [] (map->Database {:db-spec database-connection}))
