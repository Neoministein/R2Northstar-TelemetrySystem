import {primeToast} from "../../layout/AppTopbar";

export class BackendError extends Error {
    public errorCode: string;



    constructor(errorCode:string, message: string) {
        super(message);
        this.errorCode = errorCode;
    }
}

const ErrorUtils = {

    async parseResponse(resp : Response) : Promise<any> {
        const data = await resp.json();

        if (resp.ok) {
            return data;
        } else {
            resp.headers.forEach(val => console.log(val))

            const backendError = resp.headers.get('validBackendError');
            if (backendError === "true") {
                throw new BackendError(data.errorCode, data.message);
            }
            throw Error();
        }
    },

    displayError(error: Error) {
        primeToast.show({ severity: 'error', summary: error.message, life: 3000 });
    }
}

export default ErrorUtils;
