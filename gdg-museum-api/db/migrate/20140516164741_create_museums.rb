class CreateMuseums < ActiveRecord::Migration
  def change
    create_table :museums do |t|
      t.string :name
      t.string :uuid

      t.timestamps
    end
  end
end
