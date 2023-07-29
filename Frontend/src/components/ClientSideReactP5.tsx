import dynamic from "next/dynamic";
import {P5CanvasInstance} from "@p5-wrapper/react";


export interface ReactP5WrapperV2Props {
    sketchFunc(p5Instance: P5CanvasInstance): void
    shouldComponentUpdate?: boolean
}

export default function ClientSideReactP5({sketchFunc, shouldComponentUpdate} : ReactP5WrapperV2Props) {

    const ReactP5Wrapper = dynamic<typeof import("@p5-wrapper/react").ReactP5Wrapper>(
        // @ts-ignore
        () => import("@p5-wrapper/react").then(m => {
            return m.ReactP5Wrapper;
        }),
        {ssr: false}
    );


    return (
        //@ts-ignore
        <ReactP5Wrapper sketch={sketchFunc} shouldComponentUpdate={shouldComponentUpdate}/>
    );
}
