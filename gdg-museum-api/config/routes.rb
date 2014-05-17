require 'api_constraints'

GdgMuseumApi::Application.routes.draw do
  resources :arts

  resources :regions
  resources :museums
  resources :beacons

  root "beacons#index"

  namespace :api, defaults: {format: "json"} do
    scope module: :v1, constraints: ApiConstraints.new(version: 1, default: :true) do
      resources :beacons, only: [:index, :show]
      get "beacon/search", to: "beacons#search"
    end
  end
end
