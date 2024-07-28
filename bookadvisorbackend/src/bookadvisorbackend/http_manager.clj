(ns bookadvisorbackend.http-manager
    "The main controller for the user management portion of this app."
  (:require [ring.util.response :as resp]
            [cheshire.core :as cheshire]))

(defn json-response
  "Create a reponse with Json body content."
  [content]
  (let [jsoncontent (cheshire/generate-string content)]
    (-> (resp/response jsoncontent)
        (resp/content-type "application/json"))))
