import Link from 'next/link';
import React, {forwardRef, useContext, useEffect, useImperativeHandle, useRef} from 'react';
import {LayoutContext} from './context/layoutcontext';
import {Toast} from "primereact/toast";

export let primeToast;

const AppTopbar = forwardRef((props, ref) => {
    const { onMenuToggle } = useContext(LayoutContext);
    const menubuttonRef = useRef(null);
    const topbarmenuRef = useRef(null);
    const topbarmenubuttonRef = useRef(null);
    const toast = useRef(null);

    useImperativeHandle(ref, () => ({
        menubutton: menubuttonRef.current,
        topbarmenu: topbarmenuRef.current,
        topbarmenubutton: topbarmenubuttonRef.current
    }));

    useEffect(() => {
        // @ts-ignore
        primeToast = toast.current || {};
    }, []);

    return (
        <div className="layout-topbar">
            <Toast ref={toast} />
            <Link href="/">
                <a className="layout-topbar-logo">
                    <>
                        <span>Global R2Northstar Telemetry</span>
                    </>
                </a>
            </Link>

            <button ref={menubuttonRef} type="button" className="p-link layout-menu-button layout-topbar-button" onClick={onMenuToggle}>
                <i className="pi pi-bars" />
            </button>
        </div>
    );
});

export default AppTopbar;
