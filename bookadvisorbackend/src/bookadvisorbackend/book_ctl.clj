(ns bookadvisorbackend.book-ctl
  "The main controller for the book management."
  (:require [bookadvisorbackend.http-manager :as manager]
            [bookadvisorbackend.book-model :as model]
            [clojure.data.json :as json]
            [clojure.string :as string]))

(def ^:private changes
  "Count the number of changes (since the last reload)."
  (atom 0))

(defn reset-changes
  [req]
  (reset! changes 0)
  (assoc-in req [:params :message] "The change tracker has been reset."))

(defn get-chapters
  "Retrieve the chapters."
  [req]
  (let [chapters (model/get-chapters (-> req :application/component :database))]
    (println chapters)
    (-> (manager/json-response chapters))))

(defn get-plots
  "Retrieve the plots."
  [req]
  (let [plots (model/get-plots (-> req :application/component :database))]
    (println plots)
    (-> (manager/json-response plots))))

(defn get-scenes
  "Retrieve the scenes."
  [req]
  (let [scenes (model/get-scenes (-> req :application/component :database))]
    (println scenes)
    (-> (manager/json-response scenes))))

(defn create-chapter
  "Create new chapter"
  [req]
  (let [chapter (json/read-str (slurp (:body req)) :key-fn keyword)
        createdChapter (model/create-chapter (-> req :application/component :database) chapter)]
    (-> (manager/json-response createdChapter))))

(defn update-chapter
  "Update chapter"
  [req]
  (let [chapter (json/read-str (slurp (:body req)) :key-fn keyword)]
    (model/update-chapter (-> req :application/component :database) chapter)
    (manager/json-response (model/get-chapter (-> req :application/component :database) (:chapters/id chapter)))))

(defn delete-chapter
  "Delete chapter"
  [req]
  (let [id (get (string/split (:uri req) #"/") 3)]
    (model/delete-chapter (-> req :application/component :database) id)
    (manager/json-response {})))
