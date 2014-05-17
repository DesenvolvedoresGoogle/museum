json.array!(@museums) do |museum|
  json.extract! museum, :id, :name, :uuid
  json.url museum_url(museum, format: :json)
end
