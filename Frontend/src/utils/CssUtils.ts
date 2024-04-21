const CssUtils = {

    primaryColor: null,


    getPrimaryColor(root: HTMLElement) {
        if (this.primaryColor === null) {
            this.primaryColor = getComputedStyle(root).getPropertyValue('--primary-color');
        };
        return this.primaryColor;
    }
}

export default CssUtils;
