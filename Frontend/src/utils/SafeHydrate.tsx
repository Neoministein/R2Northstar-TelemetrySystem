import dynamic from "next/dynamic";
import React from "react";

const SafeHydrate = props => (
    <React.Fragment>{props.children}</React.Fragment>
)
export default dynamic(() => Promise.resolve(SafeHydrate), {
    ssr: false
})


