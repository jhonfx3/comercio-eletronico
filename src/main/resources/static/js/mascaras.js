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

function aplicaMascaraTelefone(evento) {
	var v = evento.value;
	evento.value = v.replace(/\D/g, ""); //Remove tudo o que não é dígito
	//evento.value = v.replace(/(\d{2})(\d)/, "($1)$2"); //Coloca parênteses em volta dos dois primeiros dígitos
	//evento.value = v.replace(/(\d{5})(\d)/, "$1-$2"); //Coloca parênteses em volta dos dois primeiros dígitos
	evento.value = v.replace(/(\d{2})(\d{5})(\d)/g, "($1)$2-$3"); //Coloca parênteses em volta dos dois primeiros dígitos

}

