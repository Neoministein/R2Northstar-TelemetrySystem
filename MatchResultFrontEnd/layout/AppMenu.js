import getConfig from 'next/config';
import React, { useContext } from 'react';
import AppMenuitem from './AppMenuitem';
import { LayoutContext } from './context/layoutcontext';
import { MenuProvider } from './context/menucontext';

const AppMenu = () => {
    const { layoutConfig } = useContext(LayoutContext);
    const contextPath = getConfig().publicRuntimeConfig.contextPath;
    const model = [
        {
            label: 'Home',
            items: [{ label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' }]
        },
        {
            label: 'Global Top Categories',
            items: [
                { label: 'Player Kills', icon: 'pi pi-fw pi-user', to: '/pages/player-kills' },
                { label: 'Npc Kills', icon: 'pi pi-fw pi-android', to: '/pages/npc-kills' },
                { label: 'K/D', icon: 'pi pi-fw pi-users', to: '/uikit/floatlabel' },
                { label: 'Wins', icon: 'pi pi-fw pi-exclamation-circle', to: '/uikit/invalidstate' },
                { label: 'Win Ratio', icon: 'pi pi-fw pi-percentage', to: '//pages/win-ratio' },
            ]
        },
        {
            label: 'Player Data',
            items: [
                { label: 'Player data', icon: 'pi pi-fw pi-id-card', to: '/uikit/formlayout' }
            ]
        },
        {
            label: 'Help / Issues',
            items: [
                {
                    label: 'View Source',
                    icon: 'pi pi-fw pi-search',
                    url: 'https://github.com/primefaces/sakai-react',
                    target: '_blank'
                }
            ]
        }
    ];

    return (
        <MenuProvider>
            <ul className="layout-menu">
                {model.map((item, i) => {
                    return <AppMenuitem item={item} root={true} index={i} key={item.label} />;
                })}
            </ul>
        </MenuProvider>
    );
};

export default AppMenu;
