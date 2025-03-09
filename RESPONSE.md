# RESPUESTAS

## CAMBIOS REALIZADOS:

- Se ha creado una nueva clase llamada CustomerDataBase en: src/main/java/com/breadhardit/travelagencykata/infrastructure/persistence/repository/CustomerDataBase.java para implementar el interfaz CustomersRepository
- Se ha implementado los siguentes metodos en CustomerDataBase:
  - saveCustomer(Customer customer) para guardas los customer en el repositorio.
  - parseCustomer(CustomerEntity customer) para convertir un CustomerEntity a customer
  - getCustomerById(String id): Obtengo en el respositorio un customer mediante id y usando this::parseCustomer realizo un parse para obtener el Customer
  - getCustomerByPassport(String id): Obtengo en el respositorio un customer mediante el passport y usando this::parseCustomer realizo un parse para obtener el Customer

## Patrones:

- Revisando el código, no he podído encontrar ningún antipatrón 