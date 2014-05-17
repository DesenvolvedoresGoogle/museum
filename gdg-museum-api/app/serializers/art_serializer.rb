class ArtSerializer < ActiveModel::Serializer
  attributes :id, :name, :image, :minor

  has_one :region
end
