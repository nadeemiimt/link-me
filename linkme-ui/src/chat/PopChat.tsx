import React, { useState } from 'react'
//import the css here

export const PopChat = (props: any) => {
    let hide = {
        display: 'none',
    }
    let show = {
        display: 'block'
    }
    let textRef: any = React.createRef()
    const { messages } = props

    const [chatopen, setChatopen] = useState(false)
    const toggle = (e: any) => {
        setChatopen(!chatopen)
    }

    const handleSend = (e: any) => {
        const get = props.getMessage
        get(textRef.current.value)
    }

    return (
        <div id='chatCon'>
            <div className="chat-box" style={chatopen ? show : hide}>
                <div className="header">Chat with {props.userType}</div>
                <div className="msg-area">
                    {
                        messages.map((msg: any, i: any) => (
                            i % 2 ? (
                                <p className="right"><span>{msg}</span></p>
                            ) : (
                                <p className="left"><span>{msg}</span></p>
                            )
                        ))
                    }

                </div>
                <div className="footer">
                    <input type="text" ref={textRef} />
                    <button onClick={handleSend}><i className="fa fa-paper-plane"></i></button>
                </div>
            </div>
            <div className="pop">
                <p><img onClick={toggle} src="https://p7.hiclipart.com/preview/151/758/442/iphone-imessage-messages-logo-computer-icons-message.jpg" alt="" /></p>
            </div>
        </div>
    )
}

export default PopChat