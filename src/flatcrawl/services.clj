(ns flatcrawl.services)

(defprotocol ServiceOperations
  "Collection of functionality that can be started & stopped"
  (start [this] "Starts the service")
  (stop [this] "Stops the service")
  (status [this] "Returns status of the service (stopped/running)"))

(defrecord Service [name start stop status]
  ServiceOperations
  (start [this] (start))
  (stop [this] (stop))
  (status [this] (status)))
