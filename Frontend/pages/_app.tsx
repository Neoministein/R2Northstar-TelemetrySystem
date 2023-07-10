import React from 'react';
import {LayoutProvider} from '../layout/context/layoutcontext';
import Layout from '../layout/layout';
import 'primereact/resources/primereact.css';
import 'primeflex/primeflex.css';
import 'primeicons/primeicons.css';
import '../styles/layout/layout.scss';
import SafeHydrate from "../src/utils/SafeHydrate";

export default function MyApp({ Component, pageProps }) {
    if (Component.getLayout) {
        return (
            <SafeHydrate>
                <LayoutProvider>
                    {Component.getLayout(<Component {...pageProps} />)}
                </LayoutProvider>
            </SafeHydrate>
        )
    } else {
        return (
            <SafeHydrate>
                <LayoutProvider>
                    <Layout>
                        <Component {...pageProps} />
                    </Layout>
                </LayoutProvider>
            </SafeHydrate>
        );
    }
}
