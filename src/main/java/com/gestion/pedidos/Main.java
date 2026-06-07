package com.gestion.pedidos;

import com.gestion.pedidos.model.Categoria;
import com.gestion.pedidos.model.Producto;
import com.gestion.pedidos.repository.CategoriaRepository;
import com.gestion.pedidos.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CategoriaRepository categoriaRepo = new CategoriaRepository();
    private static final ProductoRepository  productoRepo  = new ProductoRepository();

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n========== MENU PRINCIPAL ==========");
            System.out.println("1. Gestion de Categorias");
            System.out.println("2. Gestion de Productos");
            System.out.println("3. Reportes");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> menuCategorias();
                case "2" -> menuProductos();
                case "3" -> menuReportes();
                case "0" -> salir = true;
                default  -> System.out.println("Opcion invalida.");
            }
        }
        System.out.println("Saliendo del sistema...");
    }

    // =========================================================
    // MENÚ CATEGORÍAS
    // =========================================================
    private static void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Gestion de Categorias ---");
            System.out.println("1. Alta de categoria");
            System.out.println("2. Modificar categoria");
            System.out.println("3. Baja logica de categoria");
            System.out.println("4. Listar categorias activas");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> altaCategoria();
                case "2" -> modificarCategoria();
                case "3" -> bajaCategoria();
                case "4" -> listarCategorias();
                case "0" -> volver = true;
                default  -> System.out.println("Opcion invalida.");
            }
        }
    }

    // HU-03: Alta de categoría
    private static void altaCategoria() {
        System.out.println("\n-- Alta de Categoria --");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("ERROR: El nombre no puede estar vacio.");
            return;
        }

        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine().trim();

        // @PrePersist en Base setea eliminado=false y createdAt automáticamente
        Categoria nueva = Categoria.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .build();

        Categoria guardada = categoriaRepo.guardar(nueva);
        System.out.println("Categoria creada con ID: " + guardada.getId());
    }

    // HU-04: Modificar categoría
    private static void modificarCategoria() {
        System.out.println("\n-- Modificar Categoria --");
        listarCategorias();

        System.out.print("Ingrese el ID de la categoria a modificar: ");
        Long id = parseLong(scanner.nextLine().trim());
        if (id == null) { System.out.println("ID invalido."); return; }

        // Optional: manejo seguro de valor potencialmente nulo
        Optional<Categoria> opt = categoriaRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("ERROR: No existe una categoria activa con ese ID.");
            return;
        }

        Categoria cat = opt.get();
        System.out.println("Valores actuales:");
        System.out.println("  Nombre     : " + cat.getNombre());
        System.out.println("  Descripcion: " + cat.getDescripcion());

        System.out.print("Nuevo nombre (Enter para mantener): ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) cat.setNombre(nombre);

        System.out.print("Nueva descripcion (Enter para mantener): ");
        String desc = scanner.nextLine().trim();
        if (!desc.isEmpty()) cat.setDescripcion(desc);

        categoriaRepo.guardar(cat);
        System.out.println("Categoria actualizada correctamente.");
    }

    // HU-05: Baja lógica de categoría
    private static void bajaCategoria() {
        System.out.println("\n-- Baja de Categoria --");
        System.out.print("Ingrese el ID de la categoria a dar de baja: ");
        Long id = parseLong(scanner.nextLine().trim());
        if (id == null) { System.out.println("ID invalido."); return; }

        Optional<Categoria> opt = categoriaRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("ERROR: No existe una categoria activa con ese ID.");
            return;
        }

        String nombre = opt.get().getNombre();
        boolean ok = categoriaRepo.eliminarLogico(id);
        if (ok) {
            System.out.println("Categoria '" + nombre + "' dada de baja correctamente.");
        } else {
            System.out.println("ERROR: No se pudo dar de baja la categoria.");
        }
    }

    // Listado de categorías activas — Stream + lambda
    private static void listarCategorias() {
        List<Categoria> lista = categoriaRepo.listarActivos();
        if (lista.isEmpty()) {
            System.out.println("No hay categorias activas.");
            return;
        }
        System.out.println("\n  ID  | Nombre               | Descripcion");
        System.out.println("------+----------------------+-------------------------");
        lista.stream().forEach(c ->
                System.out.printf("  %-4d| %-20s | %s%n",
                        c.getId(), c.getNombre(), c.getDescripcion())
        );
    }

    // =========================================================
    // MENÚ PRODUCTOS
    // =========================================================
    private static void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Gestion de Productos ---");
            System.out.println("1. Alta de producto");
            System.out.println("2. Modificar producto");
            System.out.println("3. Baja logica de producto");
            System.out.println("4. Listar productos activos");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> altaProducto();
                case "2" -> modificarProducto();
                case "3" -> bajaProducto();
                case "4" -> listarProductos();
                case "0" -> volver = true;
                default  -> System.out.println("Opcion invalida.");
            }
        }
    }

    // HU-06: Alta de producto
    private static void altaProducto() {
        System.out.println("\n-- Alta de Producto --");

        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias activas. No se puede crear un producto.");
            return;
        }

        listarCategorias();
        System.out.print("Seleccione el ID de la categoria: ");
        Long catId = parseLong(scanner.nextLine().trim());
        if (catId == null) { System.out.println("ID invalido."); return; }

        // Optional: verificación segura antes de continuar
        Optional<Categoria> optCat = categoriaRepo.buscarPorId(catId);
        if (optCat.isEmpty() || optCat.get().isEliminado()) {
            System.out.println("ERROR: La categoria seleccionada no existe o esta inactiva.");
            return;
        }

        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) { System.out.println("ERROR: El nombre no puede estar vacio."); return; }

        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine().trim();

        System.out.print("Precio (mayor a 0): ");
        Double precio = parseDouble(scanner.nextLine().trim());
        if (precio == null || precio <= 0) {
            System.out.println("ERROR: El precio debe ser mayor a 0.");
            return;
        }

        System.out.print("Stock (mayor o igual a 0): ");
        Integer stock = parseInteger(scanner.nextLine().trim());
        if (stock == null || stock < 0) {
            System.out.println("ERROR: El stock no puede ser negativo.");
            return;
        }

        System.out.print("Imagen (URL o nombre, Enter para omitir): ");
        String imagen = scanner.nextLine().trim();

        // @PrePersist en Base setea eliminado=false y createdAt automáticamente
        Producto nuevo = Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .stock(stock)
                .imagen(imagen.isEmpty() ? null : imagen)
                .disponible(true)
                .categoria(optCat.get())
                .build();

        Producto guardado = productoRepo.guardar(nuevo);
        System.out.println("Producto creado con ID: " + guardado.getId()
                + " | Categoria: " + optCat.get().getNombre());
    }

    // HU-07: Modificar producto
    private static void modificarProducto() {
        System.out.println("\n-- Modificar Producto --");
        listarProductos();

        System.out.print("Ingrese el ID del producto a modificar: ");
        Long id = parseLong(scanner.nextLine().trim());
        if (id == null) { System.out.println("ID invalido."); return; }

        Optional<Producto> opt = productoRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("ERROR: No existe un producto activo con ese ID.");
            return;
        }

        Producto prod = opt.get();
        System.out.println("Valores actuales:");
        System.out.println("  Nombre : " + prod.getNombre());
        System.out.println("  Precio : " + prod.getPrecio());
        System.out.println("  Stock  : " + prod.getStock());

        System.out.print("Nuevo nombre (Enter para mantener): ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) prod.setNombre(nombre);

        System.out.print("Nuevo precio (Enter para mantener): ");
        String precioStr = scanner.nextLine().trim();
        if (!precioStr.isEmpty()) {
            Double precio = parseDouble(precioStr);
            if (precio == null || precio <= 0) {
                System.out.println("Precio invalido. Se conserva el valor anterior.");
            } else {
                prod.setPrecio(precio);
            }
        }

        System.out.print("Nuevo stock (Enter para mantener): ");
        String stockStr = scanner.nextLine().trim();
        if (!stockStr.isEmpty()) {
            Integer stock = parseInteger(stockStr);
            if (stock == null || stock < 0) {
                System.out.println("Stock invalido. Se conserva el valor anterior.");
            } else {
                prod.setStock(stock);
            }
        }

        productoRepo.guardar(prod);
        System.out.println("Producto actualizado correctamente.");
    }

    // HU-08: Baja lógica de producto
    private static void bajaProducto() {
        System.out.println("\n-- Baja de Producto --");
        System.out.print("Ingrese el ID del producto a dar de baja: ");
        Long id = parseLong(scanner.nextLine().trim());
        if (id == null) { System.out.println("ID invalido."); return; }

        Optional<Producto> opt = productoRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("ERROR: No existe un producto activo con ese ID.");
            return;
        }

        String nombre = opt.get().getNombre();
        boolean ok = productoRepo.eliminarLogico(id);
        if (ok) {
            System.out.println("Producto '" + nombre + "' dado de baja correctamente.");
        } else {
            System.out.println("ERROR: No se pudo dar de baja el producto.");
        }
    }

    // Listado de productos activos — Stream + lambda
    private static void listarProductos() {
        List<Producto> lista = productoRepo.listarActivos();
        if (lista.isEmpty()) {
            System.out.println("No hay productos activos.");
            return;
        }
        System.out.println("\n  ID  | Nombre               | Precio     | Stock | Categoria");
        System.out.println("------+----------------------+------------+-------+------------------");
        lista.stream().forEach(p ->
                System.out.printf("  %-4d| %-20s | %-10.2f | %-5d | %s%n",
                        p.getId(), p.getNombre(), p.getPrecio(),
                        p.getStock(), p.getCategoria().getNombre())
        );
    }

    // =========================================================
    // MENÚ REPORTES — HU-09
    // =========================================================
    private static void menuReportes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Reportes ---");
            System.out.println("1. Productos por categoria");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> reporteProductosPorCategoria();
                case "0" -> volver = true;
                default  -> System.out.println("Opcion invalida.");
            }
        }
    }

    // HU-09: Consulta JPQL — productos por categoría
    private static void reporteProductosPorCategoria() {
        System.out.println("\n-- Productos por Categoria --");

        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias activas.");
            return;
        }

        listarCategorias();
        System.out.print("Ingrese el ID de la categoria: ");
        Long id = parseLong(scanner.nextLine().trim());
        if (id == null) { System.out.println("ID invalido."); return; }

        Optional<Categoria> optCat = categoriaRepo.buscarPorId(id);
        if (optCat.isEmpty() || optCat.get().isEliminado()) {
            System.out.println("ERROR: La categoria no existe o esta inactiva.");
            return;
        }

        // Llama a buscarPorCategoria() — JPQL con TypedQuery y parámetro nombrado
        List<Producto> productos = productoRepo.buscarPorCategoria(id);

        if (productos.isEmpty()) {
            System.out.println("La categoria '" + optCat.get().getNombre()
                    + "' no tiene productos activos.");
            return;
        }

        System.out.println("Productos activos en '" + optCat.get().getNombre() + "':");
        System.out.println("  ID  | Nombre               | Precio     | Stock");
        System.out.println("------+----------------------+------------+-------");
        productos.stream().forEach(p ->
                System.out.printf("  %-4d| %-20s | %-10.2f | %d%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock())
        );
    }

    // =========================================================
    // UTILIDADES DE PARSEO
    // =========================================================
    private static Long parseLong(String s) {
        try { return Long.parseLong(s); }
        catch (NumberFormatException e) { return null; }
    }

    private static Integer parseInteger(String s) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return null; }
    }

    private static Double parseDouble(String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return null; }
    }
}