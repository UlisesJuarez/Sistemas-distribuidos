var URL = "../Servicio/rest/ws";

function limpiar() {
    get("descripcion").value = "";
    get("precio").value = "";
    get("cantidad").value = "";
    get("imagen").src = "/images/vacio.png";
    foto = null;
}

function readSingleFile(files, imagen) {
    var file = files[0];
    if (!file) return;
    var reader = new FileReader();
    reader.onload = function (e) {
        imagen.src = reader.result;
        foto = reader.result.split(',')[1];
    };
    reader.readAsDataURL(file);
}

function alta() {
    
    var cliente = new WSClient(URL);
    var producto =
    {
        descripcion: get("descripcion").value,
        precio: get("precio").value,
        cantidad: get("cantidad").value,
        foto: foto
    };
    cliente.post("alta_producto",
        {
            producto:producto
        },
        function (code, result) {
            if (code == 200)
                alert("OK");
            else
                alert(JSON.stringify(result));
        });
}