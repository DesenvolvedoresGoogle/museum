class BeaconSerializer < ActiveModel::Serializer
  attributes :id, :uuid, :major, :minor
end
