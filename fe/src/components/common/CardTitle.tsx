interface FBCardTitleProps {
  title:string
}

const FBCardTitle:React.FC<FBCardTitleProps> = ({title}) => {
  return (<div className='fb-card-title'>{title}</div>);
}

export default FBCardTitle;

