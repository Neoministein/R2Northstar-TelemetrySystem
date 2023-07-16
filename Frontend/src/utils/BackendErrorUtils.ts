export interface BackendError {
    errorCode: string
    message: string
}

const BackendErrorUtils = {

    async parseResponse(resp : Response) : Promise<any> {
        const data = await resp.json();

        if (resp.ok) {
            return data;
        } else {
            resp.headers.forEach(val => console.log(val))

            const backendError = resp.headers.get('validBackendError');
            console.log(resp.headers)
            console.log(backendError)
            if (backendError === "true") {
                throw data as BackendError;
            }
            throw Error();
        }
    }
}

export default BackendErrorUtils;
