# Book Advisor

## Table of contents

* [Introduction](#introduction)
* [Development](#development)
  * [Setup](#setup)
  * [Build project](#build-project)

## Introduction

Book Advisor is a web application that aims to help the planning and writing of books.

## Development

Book Advisor is a web application built using Clojure in the backend and Vue.js on the frontend.

### Setup

Install OpenJDK 11, Node.js >= 20.15.1, and Docker (Docker compose).

### Build and run application (development)

Build and run the backend in Docker:

```sh
./build_development.sh
```

Build and run the frontend:

```sh
cd bookadvisorfrontend
npm run dev
```

### Build and run application (production)

Build and run the backend and frontend in Docker:

```sh
./build.sh
```
