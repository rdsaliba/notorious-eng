create table model_evaluation(
model_id int(11),
asset_type_id int(11),
rmse double,
primary key(model_id,asset_type_id),
foreign key(model_id) references model(model_id),
foreign key(asset_type_id) references asset_type(asset_type_id)
);