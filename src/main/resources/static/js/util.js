function mostrarOuEsconderSenha(e) {
	event.preventDefault();
	buttonPasswd = document.getElementById("password");
	if (buttonPasswd.type == "password") {
		buttonPasswd.type = "text";
	} else {
		buttonPasswd.type = "password";
	}
}