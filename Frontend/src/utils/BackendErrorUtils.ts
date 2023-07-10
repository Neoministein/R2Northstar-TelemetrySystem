export interface BackendError {
    errorCode: string
    message: string
}

const BackendErrorUtils = {

    async parseResponse(resp : Response) : Promise<any> {
        if (resp.ok) {
            return resp.json();
        } else {
            const backendError = resp.headers.get("validBackendError");
            if (backendError === "true") {
                throw (await resp.json() as BackendError);
            }
            throw Error();
        }
    }
}

export default BackendErrorUtils;
