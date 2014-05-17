json.array!(@regions) do |region|
  json.extract! region, :id, :name, :major, :museum_id
  json.url region_url(region, format: :json)
end
