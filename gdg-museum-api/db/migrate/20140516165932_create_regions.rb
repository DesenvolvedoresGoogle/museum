class CreateRegions < ActiveRecord::Migration
  def change
    create_table :regions do |t|
      t.string :name
      t.string :major
      t.references :museum, index: true

      t.timestamps
    end
  end
end
