json.array!(@arts) do |art|
  json.extract! art, :id, :name, :image, :minor
  json.url art_url(art, format: :json)
end
