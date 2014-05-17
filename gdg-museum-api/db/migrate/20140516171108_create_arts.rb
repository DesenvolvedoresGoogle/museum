class CreateArts < ActiveRecord::Migration
  def change
    create_table :arts do |t|
      t.string :name
      t.string :image
      t.string :minor
      t.references :region

      t.timestamps
    end
  end
end
