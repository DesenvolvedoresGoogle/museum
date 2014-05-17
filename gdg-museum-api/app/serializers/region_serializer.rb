class RegionSerializer < ActiveModel::Serializer
  attributes :id, :name, :major

  has_one :museum
end
