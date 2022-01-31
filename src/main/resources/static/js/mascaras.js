function aplicaMascaraNumero(evento) {
	var texto = evento.value;
	texto = texto.replace(/[^0-9]/, '');
	console.log(texto);
	evento.value = texto;
}
function aplicaMascaraCpf(evento) {
	var texto = evento.value;
	texto = texto.replace(/\D/g, "");
	texto = texto.replace(/(\d{3})(\d)/, "$1.$2");
	texto = texto.replace(/(\d{3})(\d)/, "$1.$2");
	texto = texto.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
	console.log(texto);
	evento.value = texto;
}

function aplicaMascaraCep(evento) {
	var cep = evento.value;
	if (cep.match(/[0-9]{5}-[0-9]{3}/)) {
		//console.log("formato de cep");
	}
	evento.value = cep.replace(/^\d/, '');
	evento.value = cep.replace(/(\d{5})(\d)/, '$1-$2');
	return cep;
}