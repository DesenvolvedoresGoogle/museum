class Region < ActiveRecord::Base
  belongs_to :museum
  has_many :arts
end
