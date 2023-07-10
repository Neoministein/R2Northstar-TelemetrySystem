export class MatchStateWrapper {

    state: any = null;

    setState(state: any) {
        this.state = state
    }

    getState() : any {
        return this.state;
    }
}
