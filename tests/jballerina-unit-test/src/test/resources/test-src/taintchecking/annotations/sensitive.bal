public function main (string... args) {
    secureFunctionFirstParamSensitive("static", args[0]);
    secureFunctionSecondParamSensitive(args[0], "static");
}

public function secureFunctionFirstParamSensitive (@untainted string secureIn, string insecureIn) {
    string data = secureIn + insecureIn;
}

public function secureFunctionSecondParamSensitive (string insecureIn, @untainted string secureIn) {
    string data = secureIn + insecureIn;
}
